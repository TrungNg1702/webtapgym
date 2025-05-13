package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.MuscleMainGroupDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.MuscleMainGroup;
import com.project.WebTapGym.repositories.MuscleMainGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class MuscleMainGroupService implements IMuscleMainGroupService{

    private final MuscleMainGroupRepository muscleMainGroupRepository;

    @Override
    @Transactional
    public MuscleMainGroup createMuscleMainGroup(MuscleMainGroupDTO muscleMainGroupDTO) {
       MuscleMainGroup newMuscleMain = MuscleMainGroup
               .builder()
               .name(muscleMainGroupDTO.getName())
               .build();
        return muscleMainGroupRepository.save(newMuscleMain);
    }

    @Override
    public MuscleMainGroup getMuscleMainGroupById(Long id) throws DataNotFoundException {
        return muscleMainGroupRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Muscle main group not found"));
    }

    @Override
    public List<MuscleMainGroup> getAllMuscleMainGroup() {
        return muscleMainGroupRepository.findAll();
    }

    @Override
    @Transactional
    public MuscleMainGroup updateMuscleMainGroup(Long muscleMainId, MuscleMainGroupDTO muscleMainGroupDTO) throws DataNotFoundException {
        MuscleMainGroup existingMuscleMain = getMuscleMainGroupById(muscleMainId);
        existingMuscleMain.setName(muscleMainGroupDTO.getName());
        muscleMainGroupRepository.save(existingMuscleMain);
        return existingMuscleMain;
    }

    @Override
    @Transactional
    public void deleteMuscleMainGroup(Long id) {
        muscleMainGroupRepository.deleteById(id);
    }
}
