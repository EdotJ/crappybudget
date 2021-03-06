package com.budgeteer.api.controllers;

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

    private List<Entry> getEntries(Long fetchId) {
        List<Entry> entries;
        if (fetchId == null) {
            entries = entryService.getAllByUser();
        } else {
            entries = entryService.getAllByAccount(fetchId);
        }
        return entries;
    }

    private void writeEntries(List<Entry> entries, CSVWriter writer) {
        for (Entry entry : entries) {
            String sb = entry.getDate().format(formatter) + ","
                    + entry.getName() + ","
                    + entry.getValue() + ","
                    + entry.getCategory().getName() + ","
                    + entry.getAccount().getName() + ",";
            writer.writeNext(sb.split(","));
        }
    }
}
