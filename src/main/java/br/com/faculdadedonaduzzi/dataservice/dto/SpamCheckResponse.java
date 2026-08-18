package br.com.faculdadedonaduzzi.dataservice.dto;

public record SpamCheckResponse (String category, double confidenceScore, boolean isBlocked){
}
