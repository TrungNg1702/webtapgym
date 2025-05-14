package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.MuscleGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleGroup;
import com.project.WebTapGym.models.MuscleMainGroup;
import com.project.WebTapGym.repositories.MuscleGroupsRepository;
import com.project.WebTapGym.repositories.MuscleMainGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MuscleGroupService implements IMuscleGroupService{

    private final MuscleGroupsRepository muscleGroupsRepository;
    private final MuscleMainGroupRepository muscleMainGroupRepository;

    @Override
    @Transactional
    public MuscleGroup createMuscleGroup(MuscleGroupDTO muscleGroupDTO) throws DataNotFoundException {
        MuscleMainGroup existingMuscleMain = muscleMainGroupRepository.findById(muscleGroupDTO.getMainGroupId())
                .orElseThrow(() -> new DataNotFoundException("Muscle main group not found" + muscleGroupDTO.getMainGroupId()));

        MuscleGroup newMuscleGroup = MuscleGroup.builder()
                .groupName(muscleGroupDTO.getGroupName())
                .muscleMainGroup(existingMuscleMain)
                .build();
        return muscleGroupsRepository.save(newMuscleGroup);
    }

    @Override
    public MuscleGroup getMuscleGroupById(long id) {
        return muscleGroupsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Muscle Group not found"));
    }

    @Override
    public List<MuscleGroup> getAllMuscleGroups() {
        return muscleGroupsRepository.findAll();
    }

    public List<MuscleGroup> getMuscleGroupByMainGroup(Long muscleMainGroupId){
        return muscleGroupsRepository.findByMuscleMainGroupId(muscleMainGroupId);
    }

    @Override
    @Transactional
    public MuscleGroup updateMuscleGroup(long muscleGroupId, MuscleGroupDTO muscleGroupDTO) throws DataNotFoundException {
        MuscleGroup existingMuscleGroup = getMuscleGroupById(muscleGroupId);

        if (existingMuscleGroup != null) {
            MuscleMainGroup existingMuscleMain = muscleMainGroupRepository
                    .findById(muscleGroupDTO.getMainGroupId())
                    .orElseThrow(() -> new DataNotFoundException("Muscle main group not found" + muscleGroupDTO.getMainGroupId()));

            existingMuscleGroup.setGroupName(muscleGroupDTO.getGroupName());
            existingMuscleGroup.setMuscleMainGroup(existingMuscleMain);
            return muscleGroupsRepository.save(existingMuscleGroup);
        }

        return null;
    }

    @Override
    @Transactional
    public void deleteMuscleGroup(long id) {
        // xoa xong
        muscleGroupsRepository.deleteById(id);
    }
}
