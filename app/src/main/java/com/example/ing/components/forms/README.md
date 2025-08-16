# Form Components

This directory contains reusable form components for the Changarro Mobile application.

## Components

### FormField
A versatile form field component that supports different input types (text, date, time) with validation and icons.

### FormHeader
A header component for forms with navigation back button and title.

### FormContainer
A container component that provides consistent styling for form screens.

### FormActions
Action buttons (Accept/Cancel) for forms with consistent styling.

### ValidationError
Displays validation error messages with consistent styling.

### ToolStatusCard
A card component for displaying and editing tool status information including:
- Tool image and basic information (name, model)
- Battery level with increment/decrement controls
- Temperature with increment/decrement controls
- Availability dropdown button

## Usage

### ToolStatusCard
```kotlin
ToolStatusCard(
    tool = toolDetailData,
    batteryLevel = 50,
    temperature = 25,
    onBatteryChange = { newLevel -> 
        // Handle battery level change
    },
    onTemperatureChange = { newTemp -> 
        // Handle temperature change
    }
)
```

## Features
- Responsive design that adapts to different screen sizes
- Consistent Material Design 3 styling
- Input validation and error handling
- Accessibility support with content descriptions
- Reusable across different screens 