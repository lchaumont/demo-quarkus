package com.lchaumont.quarkus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/hello")
public class ExampleResource {

    @GET
    public Map<String, Integer> execute() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();

        List<RessourceToTreat> tasks = List.of(
                new RessourceToTreat("R1", null, List.of("R8")),
                new RessourceToTreat("R2", 2000, null),
                new RessourceToTreat("R3", 20, null),
                new RessourceToTreat("R4", null, List.of("R2", "R3")),
                new RessourceToTreat("R5", null, List.of("R4", "R6", "R7", "R8")),
                new RessourceToTreat("R6", 8000, null),
                new RessourceToTreat("R7", null, List.of("R1")),
                new RessourceToTreat("R8", null, List.of("R9")),
                new RessourceToTreat("R9", 500, null),
                new RessourceToTreat("R10", 500, null),
                new RessourceToTreat("R11", null, List.of("R10")),
                new RessourceToTreat("R12", null, List.of("R11")),
                new RessourceToTreat("R13", null, List.of("R12")),
                new RessourceToTreat("R14", null, List.of("R13")),
                new RessourceToTreat("R15", null, List.of("R14")),
                new RessourceToTreat("R16", null, List.of("R15")),
                new RessourceToTreat("R17", null, List.of("R16", "R3", "R8")),
                new RessourceToTreat("R18", null, List.of("R10", "R17", "R5")),
                new RessourceToTreat("R19", null, List.of("R11", "R18")),
                new RessourceToTreat("R20", null, List.of("R21", "R22", "R23", "R24")),
                new RessourceToTreat("R21", 2550, List.of("R20")),
                new RessourceToTreat("R22", null, List.of("R6", "R7")),
                new RessourceToTreat("R23", 20, List.of("R20")),
                new RessourceToTreat("R24", null, List.of("R2", "R5"))
        );

        ConcurrentLinkedQueue<RessourceToTreat> queue = new ConcurrentLinkedQueue<>(tasks);
        AtomicInteger activeTasksCounts = new AtomicInteger(0);
        while (!queue.isEmpty() || activeTasksCounts.get() != 0) {
            activeTasksCounts.incrementAndGet();
            RessourceToTreat ressourceToTreat = queue.poll();
            if (ressourceToTreat != null) {
                executorService.submit(new RessourceTreatmentTask(ressourceToTreat, queue, data, activeTasksCounts));
            } else {
                activeTasksCounts.decrementAndGet();
            }
        }

        return data;
    }
}