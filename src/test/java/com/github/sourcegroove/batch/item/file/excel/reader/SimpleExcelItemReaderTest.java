package com.github.sourcegroove.batch.item.file.excel.reader;

import com.github.sourcegroove.batch.item.file.excel.ExcelItemReader;
import com.github.sourcegroove.batch.item.file.excel.ExcelRowMapper;
import com.github.sourcegroove.batch.item.file.excel.ExcelRowTokenizer;
import com.github.sourcegroove.batch.item.file.excel.SimpleExcelItemReader;
import com.github.sourcegroove.batch.item.file.format.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.format.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleExcelItemReaderTest extends AbstractExcelItemReaderTest{


    public ExcelItemReader getReader(String filename, Integer sheet){
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(LocalDate.class, new LocalDateEditor());
        editors.put(LocalDateTime.class, new LocalDateTimeEditor());

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(MockUserRecord.class);
        fieldSetMapper.setCustomEditors(editors);

        ExcelRowTokenizer tokenizer = new ExcelRowTokenizer();
        tokenizer.setNames(new String[]{"recordType", "username", "firstName","lastName","dateOfBirth", "age"});
        ExcelRowMapper rowMapper = new ExcelRowMapper();
        rowMapper.setFieldSetMapper(fieldSetMapper);
        rowMapper.setRowTokenizer(tokenizer);

        Set<Integer> sheets = null;
        if(sheet != null){
            sheets = new HashSet<>();
            sheets.add(sheet);
        }

        SimpleExcelItemReader reader = new SimpleExcelItemReader();
        reader.setLinesToSkip(1);
        reader.setSheetsToRead(sheets);
        reader.setRowMapper(rowMapper);
        reader.setResource(MockFactory.getResource(filename));

        return reader;
    }
}
