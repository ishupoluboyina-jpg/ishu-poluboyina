package com.example.ai

import com.example.data.model.CareerGuidance
import com.example.data.model.UserProfile
import kotlin.math.roundToInt

object CareerAiEngine {

    fun generateGuidance(profile: UserProfile): CareerGuidance {
        val cgpa = profile.cgpa
        val backlogs = profile.backlogs
        val skillsLower = profile.skills.lowercase()

        // Algorithmic evaluation factors
        var scoreBase = (cgpa / 10.0) * 60.0 // CGPA contributes up to 60 points
        if (backlogs == 0) {
            scoreBase += 20.0 // No backlogs bonus
        } else {
            scoreBase -= (backlogs * 8.0).coerceAtMost(30.0)
        }

        // Skills bonus
        val skillPoints = listOf("kotlin", "python", "cloud", "aws", "docker", "sql", "java", "react", "machine learning")
            .count { skillsLower.contains(it) } * 3.5
        val finalScore = (scoreBase + skillPoints).coerceIn(40.0, 98.0).roundToInt()

        val riskLevel = when {
            backlogs > 1 || cgpa < 6.5 -> "HIGH"
            backlogs == 1 || cgpa < 7.5 -> "MODERATE"
            else -> "LOW"
        }

        val suggestedRoles = when {
            skillsLower.contains("cloud") || skillsLower.contains("aws") ->
                "Cloud Solutions Architect, DevOps Engineer, Site Reliability Engineer"
            skillsLower.contains("machine learning") || skillsLower.contains("python") ->
                "AI/ML Engineer, Data Scientist, Quantitative Analyst"
            skillsLower.contains("kotlin") || skillsLower.contains("android") ->
                "Android Systems Engineer, Mobile Software Architect, Full-Stack Developer"
            else ->
                "Software Development Engineer (SDE), Backend Developer, Enterprise Systems Analyst"
        }

        val skillGaps = when {
            !skillsLower.contains("docker") && !skillsLower.contains("kubernetes") ->
                "Containerization (Docker & Kubernetes), Microservices Architecture, CI/CD pipeline automation"
            !skillsLower.contains("sql") ->
                "Relational Database Indexing, Distributed Query Optimization, ACID transactions"
            else ->
                "System Design for high-throughput distributed systems, Mock Technical Coding Interviews under timed constraints"
        }

        val plan = """
1. Week 1-2: Core Data Structures & Algorithms — Solve 25 medium problems focusing on dynamic programming and graphs.
2. Week 3: Hands-on Portfolio Enhancement — Deploy your capstone application to cloud with automated GitHub Actions CI/CD.
3. Week 4: Placement Readiness — Participate in 2 peer mock interviews and take campus aptitude practice assessments.
        """.trimIndent()

        val rationale = """
Evaluated academic profile: CGPA of ${"%.2f".format(cgpa)} with $backlogs active backlog(s). ${if (backlogs == 0) "Strong academic discipline satisfies 95% of tier-1 campus recruiter eligibility criteria." else "Prioritize clearing remaining backlogs to restore eligibility for top product companies."} Found foundational competence in ${profile.skills.split(",").take(3).joinToString(", ")}.
        """.trimIndent()

        return CareerGuidance(
            studentId = profile.userId.ifBlank { "student_default" },
            studentName = profile.name.ifBlank { "Alex Mercer" },
            successScore = finalScore,
            riskLevel = riskLevel,
            readinessPercentage = (finalScore * 0.95).roundToInt(),
            suggestedRoles = suggestedRoles,
            skillGaps = skillGaps,
            actionablePlan = plan,
            rationale = rationale
        )
    }
}
