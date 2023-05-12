package com.example.master.models

data class UsageStatistics(
    val packageName: String,
    val totalTime: Long, // in milliseconds
    val date: String,
    val isSocialNetwork: Boolean
) {
    companion object {
        fun isSocialNetwork(packageName: String): Boolean {
            return packageName.contains("youtube") ||
                    packageName.contains("skype") ||
                    packageName.contains("instagram") ||
                    packageName.contains("facebook") ||
                    packageName.contains("tiktok")
        }

        fun firebaseId(packageName: String): String {
            return packageName.replace(".", "_")
        }
    }
}

