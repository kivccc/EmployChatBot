package com.dt.employ_chatbot.repository;

import com.dt.employ_chatbot.domain.CaseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborCaseRepository extends JpaRepository<CaseData,Long> {
}
