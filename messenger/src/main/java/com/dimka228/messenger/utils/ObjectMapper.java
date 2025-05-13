package com.dimka228.messenger.utils;

import org.modelmapper.ModelMapper;

public class ObjectMapper {
    private static ModelMapper modelMapper = new ModelMapper();

    public static <T> T map (Object o, Class<T> clazz){
        return modelMapper.map(o,clazz);
    }
}
