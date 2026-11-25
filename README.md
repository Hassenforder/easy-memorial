# easy-memorial


## Name

easy-memorial : Android application from my easy brand


## Description

Small Android application to build and perform a guided tour in a city.

- You can build a guided tour
- You can perform a guided tour

What is a guided tour, it is a tour where you can stop at several points to have some explanations and after that move to another one.
The nice behavior : when performing the tour, you use the camera to show the point if a key point is detected by a labeller you succeed
and the explanations give you information and you can continue. So it is a picture IAlabelled  guided tour.

## Technologies covered

- Kotlin
- KSP
- Compose
- Coroutines
- Flow
- Room (database ORM)
- Architecture (database-dao-repository-viewmodel)
- Hilt (injection framework)
- Navigation 3
- Coil (Async Image)
- External App call (Document, Camera)
- picture IA labeller
- video player/recorder

## Entities

- Tour (title, description, picture, mode, with...)
- POI (name, description, picture, media)
- Trigger (label, latitude, longitude, hint)
- Guide (tourId, poiId, triggerId, position)
- TourProgress (tourId, state, mode, with...)
- GuideProgress (guideId, state)

## Relations 

Tour : is first main entity (it described the whole tour with all points and how to see them, etc...)
TourProgress : is second main entity (it is the tour during the journey)
Guide-Tour-POI-Trigger : is a relation based on primary keys. It bind together a specific point to visit (POI) how to discover it (trigger) and the position if it is an ordered tour. In an ordered tour progress, the visitor cannot trigger a point if it is not the current point to visit.
GuideProgress-TourProgress : is the 'state' of a guide during a progress.

## Special tricks

- many entities with the same property names but joining them need different column names...
- all entities have timestamps from database
- all entities are referencable from database
- video and mlkit
- extract tour from assets and reference file in asset
- during progress, we need at least half of the database content ... : the tourprogress with the guideprogresses but also the binded entities POIs and triggers. If you have only one tour in the database it is the whole content :) and playing video or labelling picture to find given points. High pressure.

## Dependencies

- Mainly my beloved android lib (all standard required libs)
- camera video
- mlkit for the labeller

### In libs.version.toml
    [versions]
    hassenforder-android = "XX"

    [libraries]
    fr-uha-android = { module = "fr.uha.hassenforder:easy.android", version.ref = "hassenforder.android" }

### In settings-gradle.kts

    dependencyResolutionManagement {
       repositories {
            ...
            maven(url = "https://maven.pkg.github.com/hassenforder/easy-android") {
                credentials {
                    username = providers.gradleProperty("github-user").get()
                    password = providers.gradleProperty("github-pat").get()
                }
            }
            maven(
                url = "https://jitpack.io"
            )
            ...
        }
    }

## License

Copyright © 2026 Hassenforder

Software released under the MIT license.

See the LICENSE file for more information.

https://opensource.org/licenses/MIT

## Project status

Ongoing
