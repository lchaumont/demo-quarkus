package com.lchaumont.quarkus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RessourceToTreat {

    public RessourceToTreat(String name, Integer fixedValue, List<String> dependencies) {
        this.name = name;
        this.fixedValue = fixedValue;
        this.dependencies = dependencies;
    }

    private String name;
    private Integer fixedValue;
    private List<String> dependencies;

    private int requeueCount = 0;
    private List<String> satisfiedDependencies = List.of();
}
