package com.lchaumont.quarkus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RessourceTreatmentTask implements Runnable {

    public RessourceTreatmentTask(RessourceToTreat ressourceToTreat, ConcurrentLinkedQueue<RessourceToTreat> concurrentLinkedQueue, ConcurrentHashMap<String, Integer> data, AtomicInteger activeTasksCounts) {
        this.ressourceToTreat = ressourceToTreat;
        this.concurrentLinkedQueue = concurrentLinkedQueue;
        this.data = data;
        this.activeTasksCounts = activeTasksCounts;
    }

    private RessourceToTreat ressourceToTreat;
        private ConcurrentLinkedQueue<RessourceToTreat> concurrentLinkedQueue;
        private ConcurrentHashMap<String, Integer> data;
        private AtomicInteger activeTasksCounts;

        private static final int MAX_REQUEUE_COUNT = 10;

        @Override
        public void run() {
            try {
                if (ressourceToTreat.getFixedValue() != null) {
                    System.out.println("Add in dataset with fixed value " + ressourceToTreat.getName());
                    data.put(ressourceToTreat.getName(), ressourceToTreat.getFixedValue());
                } else {
                    if (ressourceToTreat.getDependencies().stream().allMatch(data::containsKey)) {
                        System.out.println("Add in dataset from dependency " + ressourceToTreat.getName());
                        data.put(ressourceToTreat.getName(), ressourceToTreat.getDependencies().stream().reduce(0, (a, b) -> a + data.get(b), Integer::sum));
                    } else {
                        if (ressourceToTreat.getRequeueCount() < MAX_REQUEUE_COUNT) {
                            System.out.println("Requeue " + ressourceToTreat.getName() + ", data: " + data);
                            List<String> newStatisfiedDependencies = ressourceToTreat.getDependencies().stream().filter(data::containsKey).toList();
                            if (ressourceToTreat.getSatisfiedDependencies() != null && newStatisfiedDependencies.size() > ressourceToTreat.getSatisfiedDependencies().size()) {
                                ressourceToTreat.setSatisfiedDependencies(newStatisfiedDependencies);
                                ressourceToTreat.setRequeueCount(0);
                            } else {
                                ressourceToTreat.setRequeueCount(ressourceToTreat.getRequeueCount() + 1);
                            }

                            concurrentLinkedQueue.add(ressourceToTreat);
                        } else {
                            System.out.println("Max requeue count reached for " + ressourceToTreat.getName() + ", data: " + data);
                        }
                    }
                }
            } finally {
                activeTasksCounts.decrementAndGet();
            }
        }
}
