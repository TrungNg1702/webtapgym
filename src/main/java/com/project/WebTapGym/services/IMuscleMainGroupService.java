package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.MuscleMainGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleMainGroup;

import java.util.List;

public interface IMuscleMainGroupService {
    MuscleMainGroup createMuscleMainGroup(MuscleMainGroupDTO muscleMainGroupDTO);

    MuscleMainGroup getMuscleMainGroupById(Long id) throws DataNotFoundException;

    List<MuscleMainGroup> getAllMuscleMainGroup();

    MuscleMainGroup updateMuscleMainGroup(Long muscleMainId, MuscleMainGroupDTO muscleMainGroupDTO) throws DataNotFoundException;

    void deleteMuscleMainGroup(Long id);
}
