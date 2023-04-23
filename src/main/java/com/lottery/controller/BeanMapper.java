package com.lottery.controller;

import com.lottery.controller.dto.CreateLotteryRequestDto;
import com.lottery.controller.dto.RegisterParticipantRequestDto;
import com.lottery.controller.dto.SubmissionRequestDto;
import com.lottery.service.CreateLotteryRequest;
import com.lottery.service.RegisterParticipantRequest;
import com.lottery.service.SubmissionRequest;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Component
public class BeanMapper extends ConfigurableMapper {

    private MapperFactory factory;
    private final List<Converter<?, ?>> converters;
    private final List<Mapper<?, ?>> mappers;

    @Autowired(required = false)
    public BeanMapper() {
        this(null, null);
    }

    @Autowired(required = false)
    public BeanMapper(final List<Converter<?, ?>> converters) {
        this(converters, null);
    }

    @Autowired(required = false)
    public BeanMapper(List<Converter<?, ?>> converters, List<Mapper<?, ?>> mappers) {
        super(false);
        this.converters = converters != null ? converters : Collections.emptyList();
        this.mappers = mappers != null ? mappers : Collections.emptyList();
    }

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void configure(MapperFactory factory) {
        this.factory = factory;
        // Lotteries
        factory.classMap(CreateLotteryRequestDto.class, CreateLotteryRequest.class).byDefault().register();
        // Participants
        factory.classMap(RegisterParticipantRequestDto.class, RegisterParticipantRequest.class).byDefault().register();
        // Submissions
        factory.classMap(SubmissionRequestDto.class, SubmissionRequest.class).byDefault().register();
        addAllSpringBeans();
    }

    @Override
    protected void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder) {
        // Nothing to configure for now
    }

    /**
     * Constructs and registers a {@link ClassMapBuilder} into the {@link MapperFactory} using a {@link Mapper}.
     *
     * @param mapper
     */
    @SuppressWarnings("rawtypes")
    public void addMapper(Mapper<?, ?> mapper) {
        factory.classMap(mapper.getAType(), mapper.getBType())
                .byDefault()
                .customize((Mapper) mapper)
                .register();
    }

    /**
     * Registers a {@link Converter} into the {@link ConverterFactory}.
     *
     * @param converter
     */
    public void addConverter(Converter<?, ?> converter) {
        factory.getConverterFactory().registerConverter(converter);
    }

    private void addAllSpringBeans() {
        mappers.forEach(this::addMapper);
        converters.forEach(this::addConverter);
    }
}
