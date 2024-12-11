package com.bin.sm.executor.support;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

public enum BlockingQueueTypeEnum {

    /**
     * {@link java.util.concurrent.ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3, "LinkedBlockingDeque"),

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE(4, "SynchronousQueue"),

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5, "LinkedTransferQueue"),

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE(6, "PriorityBlockingQueue"),

    /**
     * {@link ResizableLinkedBlockingQueue}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableLinkedBlockingQueue");


    private final Integer type;


    private final String name;

    BlockingQueueTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final int DEFAULT_CAPACITY = 1024;



    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static BlockingQueue<Runnable> createBlockingQueue(String blockingQueueName, Integer capacity) {
        BlockingQueue<Runnable> blockingQueue = null;
        BlockingQueueTypeEnum queueTypeEnum = Stream.of(BlockingQueueTypeEnum.values())
                .filter(each -> Objects.equals(each.name, blockingQueueName))
                .findFirst()
                .orElse(null);
        if (queueTypeEnum != null) {
            blockingQueue = createBlockingQueue(queueTypeEnum.type, capacity);
            if (Objects.equals(blockingQueue.getClass().getSimpleName(), blockingQueueName)) {
                return blockingQueue;
            }
        }


        if (capacity == null || capacity <= 0) {
            capacity = DEFAULT_CAPACITY;
        }
        blockingQueue = new LinkedBlockingQueue<>(capacity);;
        return blockingQueue;
    }

    public static BlockingQueue<Runnable> createBlockingQueue(int type, Integer capacity) {
        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(type, ARRAY_BLOCKING_QUEUE.type)) {
            blockingQueue = new ArrayBlockingQueue<>(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new LinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_DEQUE.type)) {
            blockingQueue = new LinkedBlockingDeque<>(capacity);
        } else if (Objects.equals(type, SYNCHRONOUS_QUEUE.type)) {
            blockingQueue = new SynchronousQueue<>();
        } else if (Objects.equals(type, LINKED_TRANSFER_QUEUE.type)) {
            blockingQueue = new LinkedTransferQueue<>();
        } else if (Objects.equals(type, PRIORITY_BLOCKING_QUEUE.type)) {
            blockingQueue = new PriorityBlockingQueue<>(capacity);
        } else if (Objects.equals(type, RESIZABLE_LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new ResizableLinkedBlockingQueue<>(capacity);
        }
        blockingQueue = new LinkedBlockingQueue<>(capacity);
        return blockingQueue;
    }

    public static String getBlockingQueueNameByType(int type) {
        Optional<BlockingQueueTypeEnum> queueTypeEnum = Arrays.stream(BlockingQueueTypeEnum.values())
                .filter(each -> each.type == type)
                .findFirst();
        return queueTypeEnum.map(each -> each.name).orElse("");
    }

    public static BlockingQueueTypeEnum getBlockingQueueTypeEnumByName(String name) {
        Optional<BlockingQueueTypeEnum> queueTypeEnum = Arrays.stream(BlockingQueueTypeEnum.values())
                .filter(each -> each.name.equals(name))
                .findFirst();
        return queueTypeEnum.orElse(LINKED_BLOCKING_QUEUE);
    }
}