package com.github.sourcegroove.batch.item.file.model.delimited;

import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.model.RecordLayout;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedRecordLayout implements RecordLayout {

    private DelimitedFileLayout layout;
    private Class targetType;
    private String prefix = "*";
    private List<String> fieldNames = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public static DelimitedRecordLayout of(DelimitedFileLayout layout, Class targetType){
        DelimitedRecordLayout r = new DelimitedRecordLayout();
        r.layout = layout;
        r.targetType = targetType;
        return r;
    }

    public DelimitedFileLayout build(){
        return this.layout;
    }
    public DelimitedFileLayout and(){
        return this.layout;
    }
    public DelimitedRecordLayout prefix(String prefix){
        this.prefix = prefix;
        return this;
    }
    public DelimitedRecordLayout column(String name){
        this.fieldNames.add(name);
        return this;
    }
    public DelimitedRecordLayout editor(Class<?> type, PropertyEditor editor){
        this.editors.put(type, editor);
        return this;
    }

    public String getPrefix(){
        return this.prefix;
    }
    public Class getTargetType(){
        return this.targetType;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public List<String> getFieldNames(){
        return this.fieldNames;
    }

    public FieldSetMapper getFieldSetMapper(){
        BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(this.targetType);
        mapper.setCustomEditors(this.editors);
        return mapper;
    }
    public ExtractorLineAggregator getLineAggregator() {
        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(getFieldNameArray());

        FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(this.editors);

        DelimitedLineAggregator aggregator = new DelimitedLineAggregator();
        aggregator.setFieldExtractor(fieldExtractor);

        return aggregator;
    }

    public LineTokenizer getLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(getFieldNameArray());
        return tokenizer;
    }

    private String[] getFieldNameArray() {
        return this.fieldNames.toArray(new String[this.fieldNames.size()]);
    }
}