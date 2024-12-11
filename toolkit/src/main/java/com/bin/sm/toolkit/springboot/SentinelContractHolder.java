//package com.bin.sm.springboot;
//
//import feign.Contract;
//import feign.MethodMetadata;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class SentinelContractHolder implements Contract {
//    private final Contract delegate;
//    public static final Map<String, MethodMetadata> METADATA_MAP = new HashMap();
//
//    public SentinelContractHolder(Contract delegate) {
//        this.delegate = delegate;
//    }
//
//    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
//        List<MethodMetadata> metadatas = this.delegate.parseAndValidateMetadata(targetType);
//        metadatas.forEach((metadata) -> {
//            METADATA_MAP.put(targetType.getName() + metadata.configKey(), metadata);
//        });
//        return metadatas;
//    }
//}