package br.com.faculdadedonaduzzi.dataservice.repository;

import br.com.faculdadedonaduzzi.dataservice.model.FishAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishRepository extends JpaRepository<FishAnalysisEntity, Long> {
}
