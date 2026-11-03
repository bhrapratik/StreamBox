package com.streambox.core.design

enum class AppTheme(
    val label: String,
    val background: Long,
    val surface: Long,
    val primary: Long,
    val muted: Long,
) {
    MidnightTeal("Midnight Teal", 0xFF0B1220, 0xFF16213A, 0xFF00C2A8, 0xFF9AA8C0),
    Purple("Purple Night", 0xFF1A0530, 0xFF2A0A4A, 0xFF9B30FF, 0xFFB9A8CC),
    CinemaRed("Cinema Red", 0xFF111111, 0xFF1E1E1E, 0xFFE50914, 0xFFA0A0A0),
    DeepOcean("Deep Ocean", 0xFF07152B, 0xFF102544, 0xFF3D8BFF, 0xFF8FA3C2),
    WarmAmber("Warm Amber", 0xFF14110F, 0xFF241E1A, 0xFFFFB020, 0xFFB5A89A);

    companion object {
        val Default = MidnightTeal
    }
}