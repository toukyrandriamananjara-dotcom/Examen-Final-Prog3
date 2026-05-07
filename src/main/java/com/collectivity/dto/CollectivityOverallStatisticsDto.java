package com.collectivity.dto;

public class CollectivityOverallStatisticsDto {
    private CollectivityInfoDto collectivityInformation;
    private int newMembersNumber;
    private double overallMemberCurrentDuePercentage;

    // Getters and setters
    public CollectivityInfoDto getCollectivityInformation() { return collectivityInformation; }
    public void setCollectivityInformation(CollectivityInfoDto collectivityInformation) { this.collectivityInformation = collectivityInformation; }
    public int getNewMembersNumber() { return newMembersNumber; }
    public void setNewMembersNumber(int newMembersNumber) { this.newMembersNumber = newMembersNumber; }
    public double getOverallMemberCurrentDuePercentage() { return overallMemberCurrentDuePercentage; }
    public void setOverallMemberCurrentDuePercentage(double overallMemberCurrentDuePercentage) { this.overallMemberCurrentDuePercentage = overallMemberCurrentDuePercentage; }
}