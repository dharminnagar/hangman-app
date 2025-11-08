# Theme Updates for Game History UI

## Overview
Updated all Game History UI components to match the original Hangman app's theme, ensuring consistent appearance across light and dark modes.

## Theme Colors Used

### From App Theme (`themes.xml`)
- `?attr/backgroundMainColor` - Main screen background (light: #FFFFFF, dark: #1F1F1F)
- `?attr/hangmanGameBoardViewBackgroundColor` - Card backgrounds (light: #EDEDED, dark: #2C2C2C)
- `?attr/hangmanGameBoardViewTextColor` - Primary text color (light: black, dark: white)

### From Colors (`colors.xml`)
- `@color/hangman_used_correct` (#4CAF50) - For wins and correct letter attempts
- `@color/hangman_used_wrong` (#F44336) - For losses and wrong letter attempts

## Files Updated

### 1. activity_game_history.xml
**Changes:**
- Added `android:background="?attr/backgroundMainColor"` to root layout
- Updated statistics card with `app:cardBackgroundColor="?attr/hangmanGameBoardViewBackgroundColor"`
- Applied `android:textColor="?attr/hangmanGameBoardViewTextColor"` to all TextViews
- Used `android:alpha="0.7"` for secondary text instead of hardcoded #666 colors
- Used `android:alpha="0.5"` for tertiary text instead of hardcoded #999 colors

### 2. item_game_history.xml
**Changes:**
- Added `app:cardBackgroundColor="?attr/hangmanGameBoardViewBackgroundColor"` to card
- Updated word TextView with `android:textColor="?attr/hangmanGameBoardViewTextColor"`
- Updated attempts TextView with theme color and alpha for proper contrast
- Updated date TextView with theme color and alpha
- Updated info icon tint to use `?attr/hangmanGameBoardViewTextColor` with alpha

### 3. dialog_game_detail.xml
**Changes:**
- Added `android:background="?attr/backgroundMainColor"` to ScrollView
- Updated all card backgrounds with `app:cardBackgroundColor="?attr/hangmanGameBoardViewBackgroundColor"`
- Applied theme colors to all TextViews throughout the dialog
- Updated "Game Details" header with theme text color
- Updated Close button text color
- Updated word display with theme text color
- Updated all info labels with theme color and alpha
- Updated divider View to use theme color with alpha instead of hardcoded #E0E0E0
- Updated "Letter Tries" section header with theme color

### 4. item_letter_try.xml
**Changes:**
- Updated try number TextView with `android:textColor="?attr/hangmanGameBoardViewTextColor"` and alpha
- Updated letter TextView with theme text color
- Result indicator and result text already used color resources (@color/hangman_used_correct or @color/hangman_used_wrong)

## Dark Mode Support

All theme attributes automatically adapt to dark mode:

**Light Mode:**
- Background: White (#FFFFFF)
- Card backgrounds: Light gray (#EDEDED)
- Text: Black

**Dark Mode:**
- Background: Dark (#1F1F1F)
- Card backgrounds: Darker gray (#2C2C2C)
- Text: White

## Alpha Values for Text Hierarchy

- Primary text: `1.0` (no alpha) - Main content like word, statistics
- Secondary text: `0.7` alpha - Supporting information like "Total", "Wins", attempts count
- Tertiary text: `0.5` alpha - Least important like dates, try numbers

This creates proper visual hierarchy while maintaining readability in both themes.

## Result

The Game History UI now perfectly matches the Hangman app's visual style:
- Consistent with home screen and game screen appearance
- Proper dark mode support
- Uses the same color palette and theme attributes
- Maintains visual hierarchy with appropriate alpha values
- Win/loss colors match the game's existing color scheme
