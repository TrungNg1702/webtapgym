package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
