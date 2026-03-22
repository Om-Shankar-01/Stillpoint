package com.example.stillpoint.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.stillpoint.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Elegant serif for headings and titles
val headingFontName: GoogleFont = GoogleFont("Google Sans Flex")
// Modern, airy sans-serif for body and UI elements
val bodyFontName: GoogleFont = GoogleFont("Plus Jakarta Sans")

val headingFontFamily = FontFamily(
    Font(googleFont = headingFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = headingFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = headingFontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = headingFontName, fontProvider = provider, weight = FontWeight.Bold)
)

val bodyFontFamily = FontFamily(
    Font(googleFont = bodyFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = bodyFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = bodyFontName, fontProvider = provider, weight = FontWeight.SemiBold)
)

// Set of Material typography styles optimized for reading and calm
val Typography = Typography(
    // Display Styles - used for large headers
    displayLarge = TextStyle(
        fontFamily = headingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = headingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = headingFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),

    // Title Styles - used for secondary headers and card titles
    titleLarge = TextStyle(
        fontFamily = headingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = headingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = bodyFontFamily, // Using sans for small UI titles for better legibility
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body Styles - optimized for long-form reading comfort
    bodyLarge = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp, // Generous line height (approx 1.55x)
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),

    // Label Styles - used for buttons and metadata
    labelLarge = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
