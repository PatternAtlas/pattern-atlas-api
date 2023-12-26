package io.github.patternatlas.api.rest.controller;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.rest.dto.UpdateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.service.ConcreteSolutionDescriptorService;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping(value = "/concrete-solution-descriptors", produces = "application/json")
public class ConcreteSolutionDescriptorController {

    ConcreteSolutionDescriptorService concreteSolutionDescriptorService;

    @GetMapping
    public List<ConcreteSolutionDescriptor> getConcreteSolutionDescriptors() {
        return this.concreteSolutionDescriptorService.getAllConcreteSolutionDescriptors();
    }

    @GetMapping("/{id}")
    public ConcreteSolutionDescriptor getConcreteSolutionDescriptor(@PathVariable java.util.UUID id) {
        return this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(id);
    }

    @PostMapping
    public ConcreteSolutionDescriptor createConcreteSolutionDescriptor(@RequestBody CreateConcreteSolutionDescriptorRequest request) {
        return this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(request);
    }

    @PutMapping("/{id}")
    public ConcreteSolutionDescriptor updateConcreteSolutionDescriptor(@PathVariable java.util.UUID id, @RequestBody UpdateConcreteSolutionDescriptorRequest updateRequest) {
        return this.concreteSolutionDescriptorService.updateConcreteSolutionDescriptor(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteConcreteSolutionDescriptor(@PathVariable java.util.UUID id) {
        this.concreteSolutionDescriptorService.deleteConcreteSolutionDescriptor(id);
    }
}
