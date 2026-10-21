package br.com.faculdadedonaduzzi.dataservice.repository;

import br.com.faculdadedonaduzzi.dataservice.model.FishAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FishRepository extends JpaRepository<FishAnalysisEntity, Long> {
}
