package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.MuscleMainGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleMainGroup;
import com.project.WebTapGym.services.MuscleMainGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/muscle_main_groups")
@RequiredArgsConstructor
public class MuscleMainGroupController {

    private final MuscleMainGroupService muscleMainGroupService;

    @PostMapping("")
    public ResponseEntity<?> createMuscleMain(
            @Valid @RequestBody MuscleMainGroupDTO muscleMainGroupDTO,
            BindingResult result
            )
    {
        if (result.hasErrors()) {
            List<String> errorMessages =  result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        muscleMainGroupService.createMuscleMainGroup(muscleMainGroupDTO);

        return ResponseEntity.ok("Thêm mới thành công");
    }

    @GetMapping("")
    public ResponseEntity<List<MuscleMainGroup>> getAllMuscleMain(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        List<MuscleMainGroup> muscleMainGroups = muscleMainGroupService.getAllMuscleMainGroup();
        return ResponseEntity.ok(muscleMainGroups);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMuscleMainGroup(
            @PathVariable Long id,
            @Valid @RequestBody MuscleMainGroupDTO muscleMainGroupDTO ) throws DataNotFoundException {
        muscleMainGroupService.updateMuscleMainGroup(id, muscleMainGroupDTO);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMuscleMain(@PathVariable Long id){
        muscleMainGroupService.deleteMuscleMainGroup(id);
        return ResponseEntity.ok("Xóa thành công");
    }
}
