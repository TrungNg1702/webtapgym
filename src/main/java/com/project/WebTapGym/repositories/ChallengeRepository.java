package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
