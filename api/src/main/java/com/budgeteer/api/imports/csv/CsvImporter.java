package com.budgeteer.api.imports.csv;

import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.dto.imports.CsvImportRequest;
import com.budgeteer.api.imports.ImportEntry;
import com.budgeteer.api.imports.Importer;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.service.CategoryService;
import com.budgeteer.api.service.EntryService;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Singleton
public class CsvImporter implements Importer<CsvImportRequest, CsvImporterData, ImportResult> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${importer.batchSize:1000}")
    private int importBatchSize;

    @Inject
    EntryService entryService;

    @Inject
    CategoryService categoryService;

    @Inject
    SecurityService securityService;

    @Override
    public void validateRequest(CsvImportRequest request) {
        if (!StringUtils.hasText(request.getNameHeader())) {
            throw UnsatisfiedRouteException.create(Argument.of(String.class, "nameHeader"));
        }
        if (!StringUtils.hasText(request.getDateHeader())) {
            throw UnsatisfiedRouteException.create(Argument.of(String.class, "dateHeader"));
        }
        if (!StringUtils.hasText(request.getValueHeader())) {
            throw UnsatisfiedRouteException.create(Argument.of(String.class, "valueHeader"));
        }
        if (request.getAccountId() == null) {
            throw UnsatisfiedRouteException.create(Argument.of(String.class, "accountId"));
        }
    }

    @Override
    public ImportResult getData(CsvImporterData data) throws ParseException {
        CsvToBean<ImportEntry> csvToBean = new CsvToBean<>();
        HeaderColumnNameTranslateMappingStrategy<ImportEntry> strat = new HeaderColumnNameTranslateMappingStrategy<>();
        strat.setType(ImportEntry.class);
        strat.setColumnMapping(data.getMappings());
        strat.ignoreFields(getIgnoredFields(data.getMappings()));

        List<ImportEntry> entries;
        AtomicInteger missedLineCount = new AtomicInteger();
        try {
            InputStream is = data.getFileUpload().getInputStream();
            CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            csvToBean.setMappingStrategy(strat);
            csvToBean.setCsvReader(reader);
            csvToBean.setFilter(line -> {
                boolean allValuesFilled = Arrays.stream(line).allMatch(StringUtils::hasText);
                boolean isValid = line.length == data.getMappings().size() && allValuesFilled;
                if (!isValid) {
                    missedLineCount.getAndIncrement();
                }
                return isValid;
            });
            entries = csvToBean.parse();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new ParseException("Unable to parse the CSV", 0);
        }
        return new ImportResult(entries, missedLineCount.get());
    }

    private MultiValuedMap<Class<?>, Field> getIgnoredFields(Map<String, String> mappings) {
        MultiValuedMap<Class<?>, Field> ignore = new ArrayListValuedHashMap<>();
        addIgnoredFieldIfNotPresent(ignore, "description", mappings);
        addIgnoredFieldIfNotPresent(ignore, "isExpense", mappings);
        addIgnoredFieldIfNotPresent(ignore, "category", mappings);
        return ignore;
    }

    private void addIgnoredFieldIfNotPresent(MultiValuedMap<Class<?>, Field> ignore,
                                             String field,
                                             Map<String, String> mappings) {
        if (mappings.containsValue(field)) {
            return;
        }
        try {
            ignore.put(ImportEntry.class, ImportEntry.class.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            logger.error("Unable to find field " + field + " when importing");
        }
    }

    public void parse(List<ImportEntry> importEntries, Account account, boolean shouldAddUnknownCategories) {
        List<Entry> entries = new ArrayList<>();
        Long userId = (Long) securityService.getAuthentication().get().getAttributes().get("id");
        Map<String, Category> categoriesByName = categoryService.getAll(userId)
                .stream().collect(Collectors.toMap(Category::getName, c -> c));
        for (ImportEntry importEntry : importEntries) {
            Entry entry = importEntry.transform();
            entry.setAccount(account);
            String catName = StringUtils.hasText(importEntry.getCategory()) ? importEntry.getCategory() : "default";
            if (categoriesByName.get(catName) != null) {
                entry.setCategory(categoriesByName.get(catName));
            } else if (shouldAddUnknownCategories) {
                SingleCategoryDto categoryDto = new SingleCategoryDto();
                categoryDto.setName(catName);
                categoryDto.setUserId(userId);
                Category category = categoryService.create(categoryDto);
                entry.setCategory(category);
                categoriesByName.put(catName, category);
            }
            entry.setUser(account.getUser());
            entries.add(entry);
            if (entries.size() % importBatchSize == 0 && entries.size() > 0) {
                entryService.saveAllEntries(entries);
                entries.clear();
            }
        }
        entryService.saveAllEntries(entries);
    }
}