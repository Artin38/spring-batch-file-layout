package com.github.sourcegroove.batch.item.file.layout.delimited;

import com.github.sourcegroove.batch.item.file.writer.composite.CompositeFlatFileFieldExtractor;
import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private char qualifier = '"';
    private String delimiter = ",";
    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public DelimitedFileLayout(){
        this.editor(LocalDate.class, new LocalDateEditor());
        this.editor(LocalDateTime.class, new LocalDateTimeEditor());
    }

    public DelimitedFileLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedFileLayout qualifier(char qualifier) {
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedFileLayout delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }
    public DelimitedFileLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public DelimitedFileLayout record(Class targetType) {
        if(this.targetType != null){
            throw new IllegalArgumentException("Record already defined");
        }
        this.targetType = targetType;
        return this;
    }
    public DelimitedFileLayout column(String name){
        this.columns.add(name);
        return this;
    }

    public DelimitedFileLayout layout(){
        return this;
    }

    public FlatFileItemReader getItemReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(getColumns());
        tokenizer.setQuoteCharacter(this.qualifier);
        tokenizer.setDelimiter(this.delimiter);

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(this.targetType);
        fieldSetMapper.setCustomEditors(this.editors);

        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);

        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

    public FlatFileItemWriter getItemWriter() {

        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(getColumns());

        CompositeFlatFileFieldExtractor fieldExtractor = new CompositeFlatFileFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(this.editors);

        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setFieldExtractor(extractor);
        lineAggregator.setDelimiter(this.delimiter);

        FlatFileItemWriter writer = new FlatFileItemWriter();
        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    private String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }
}