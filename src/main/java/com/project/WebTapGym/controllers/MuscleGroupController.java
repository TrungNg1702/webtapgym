package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.MuscleGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleGroup;
import com.project.WebTapGym.services.MuscleGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/musclegroups")
//@Validated
@RequiredArgsConstructor

public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;


    @PostMapping("")
    public ResponseEntity<?> createMuscleGroup(
            @Valid @RequestBody MuscleGroupDTO muscleGroupDTO,
            BindingResult result ) throws DataNotFoundException {
        if (result.hasErrors()) {
            List<String> errorMessages= result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        muscleGroupService.createMuscleGroup(muscleGroupDTO);
        return ResponseEntity.ok("Thêm mới thành công " + muscleGroupDTO.getGroupName() );
    }

    // Hien thi cac nhom co
    @GetMapping("")
    public ResponseEntity<List<MuscleGroup>> getAllMuscleGroups(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        List<MuscleGroup> muscleGroups = muscleGroupService.getAllMuscleGroups();
        return ResponseEntity.ok(muscleGroups);
    }

    @GetMapping("/main_group/{mainGroupId}")
    public ResponseEntity<List<MuscleGroup>> getMuscleGroupsByMainGroupId(@PathVariable Long mainGroupId)
    {
        List<MuscleGroup> muscleGroups = muscleGroupService.getMuscleGroupByMainGroup(mainGroupId);
        return ResponseEntity.ok(muscleGroups);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMuscleGroup(
            @PathVariable Long id,
            @RequestBody MuscleGroupDTO muscleGroupDTO) throws DataNotFoundException {
        muscleGroupService.updateMuscleGroup(id, muscleGroupDTO);
        return ResponseEntity.ok("update Muscle Groups");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMuscleGroup(@PathVariable Long id) {
        muscleGroupService.deleteMuscleGroup(id);
        return ResponseEntity.ok("delete Muscle Groups");
    }
}
