package com.budgeteer.api.controllers;

import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.service.EntryService;
import com.opencsv.CSVWriter;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller("${api.base-path}/export")
@Produces
public class ExportController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EntryService entryService;

    public ExportController(EntryService entryService) {
        this.entryService = entryService;
    }

    @Get("{?fetchId}")
    @Produces("text/csv")
    public StreamedFile exportData(@Nullable Long fetchId) {
        List<Entry> entries = getEntries(fetchId);
        if (entries.size() == 0) {
            throw new ResourceNotFoundException("NOT_FOUND", "ENTRY", "No entries are present", "No entries for user");
        }
        String username = entries.get(0).getUser().getUsername();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));
        writer.writeNext("date,name,value,category,account".split(","));
        writeEntries(entries, writer);
        try {
            writer.close();
            baos.close();
        } catch (IOException e) {
            logger.error("Could not close writer");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        String contentDisposition = username + "-" + formatter.format(LocalDateTime.now()) + ".csv";
        return new StreamedFile(bais, new MediaType("text/csv")).attach(contentDisposition);
    }

    private List<Entry> getEntries(Long accountId) {
        List<Entry> entries;
        if (accountId == null) {
            entries = entryService.getAllByUser();
        } else {
            entries = entryService.getAllByAccount(accountId);
        }
        return entries;
    }

    private void writeEntries(List<Entry> entries, CSVWriter writer) {
        for (Entry entry : entries) {
            StringBuilder sb = new StringBuilder(entry.getDate().format(formatter));
            sb.append(",\"").append(entry.getName()).append("\",").append(entry.getValue()).append(",");
            if (entry.getCategory() != null) {
                sb.append("\"").append(entry.getCategory().getName()).append("\"");
            }
            sb.append(",\"");
            sb.append(entry.getAccount().getName()).append("\"");

            writer.writeNext(sb.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
        }
    }
}
