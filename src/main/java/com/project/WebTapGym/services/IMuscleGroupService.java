package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.MuscleGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleGroup;

import java.util.List;

public interface IMuscleGroupService {
    MuscleGroup createMuscleGroup(MuscleGroupDTO muscleGroup) throws DataNotFoundException;

    MuscleGroup getMuscleGroupById(long id);

    List<MuscleGroup> getAllMuscleGroups();

    MuscleGroup updateMuscleGroup(long muscleGroupId, MuscleGroupDTO muscleGroup) throws DataNotFoundException;

    void deleteMuscleGroup(long id);
}
