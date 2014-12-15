package edu.security.generator;

public interface ChallengeGenerator {
	public String getChallenge();	
	public boolean authenticate(String answers);
}
