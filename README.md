# FITFUSION

### THE INDUSTRIAL-GRADE DIGITAL WARDROBE AND AI STYLING ENGINE.

---

## THE VISION

FitFusion is a hard-nosed digital inventory and styling system designed to strip the friction from personal apparel management. It enforces order on physical closets by digitizing garments and executing algorithmic coordination logic.

---

## STATUS: ALPHA / WORK IN PROGRESS

This codebase is currently in active development. Features are subject to architectural refactoring.

---

## TECH STACK

* **Language**: 100% Kotlin
* **UI Framework**: Jetpack Compose
* **Architecture**: MVVM
* **Local Storage**: Room Database (SQLite)
* **Concurrency**: Kotlin Coroutines & StateFlow
* **AI Integration**: Google Gemini 2.5 Flash API

---

## CORE MODULES

* **Closet**: Automatic categorization pipeline. Leverages the Google Gemini 2.5 Flash API to parse raw garment images, extract precise attributes, and index them into local storage.
* **Studio**: Generative outfit orchestrator. Feeds digitized closet inventories and contextual query parameters into Gemini 2.5 Flash to synthesize optimized outfit configurations.

---

## ARCHITECTURE

Engineered for performance and zero-latency local operations:
* **UI Layer**: Jetpack Compose. Rendered with a strict brutalist aesthetic using NavyDeep (#0A1128) and BeigeAccent (#F4EBE1) tokens, enforcing 0.dp borders and sharp, raw text presentation.
* **Architecture Pattern**: MVVM (Model-View-ViewModel) with unidirectional data flow. State is preserved and exposed via Kotlin Coroutines and cold StateFlow streams.
* **Persistence Layer**: Room Database. A robust, local SQLite schema that caches garment metadata and indexing vectors to ensure offline functionality.

---

## NEXT DIRECTIVES

* **Phase 1: Context-Aware Styling**: Integration of weather API inputs and occasion-based toggle matrices to filter Gemini prompts dynamically.
* **Phase 2: Cloud Sync & Migration**: Transitioning local Room schemas to a distributed Firebase Cloud Firestore instance for cross-device state synchronization.
* **Phase 3: VTON (Virtual Try-On)**: Embedding a generative mannequin overlay model to simulate outfit fitment on user-provided profile templates.
