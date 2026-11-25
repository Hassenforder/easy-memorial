package fr.uha.hassenforder.easy.memorial.database.feed

import android.net.Uri
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.Trigger
import fr.uha.hassenforder.easy.memorial.model.WithHelper

class MulhouseTourBuilder (
) {
    private fun uriAssetBuilder (filename : String) : Uri {
        return Uri.Builder()
            .scheme("file")
            .encodedAuthority("/android_asset")
            .encodedPath(filename)
            .build()
    }

    private fun buildMulhouseGuide1Tour() : FullGuide {
        return FullGuide(
            guide = Guide(0, 0, 0, 0, 1),
            poi = POI(
                0,
                "une église typique",
                "Une église qui est dans la vieille ville de Mulhouse. On peut voir les contreforts qui permettent une résistance accrue aux seismes",
                picture = uriAssetBuilder("church_1.jpg"),
            ),
            trigger = Trigger(
                0,
                "church",
                "", "", "Faut trouver une église dans le coin"
            )
        )
    }

    private fun buildMulhouseGuide2Tour() : FullGuide {
        return FullGuide(
            guide = Guide(0, 0, 0, 0, 2),
            poi = POI(
                0,
                "un chateau typique",
                "Un chateau qui est loin de Mulhouse. Il n'a pas résisté, il est en ruine",
                picture = uriAssetBuilder("castel_1.jpg"),
            ),
            trigger = Trigger(
                0,
                "castel",
                "", "", "Faut trouver un chateau à 15 km, aller hop faut marcher"
            )
        )
    }

    private fun buildMulhouseGuide3Tour() : FullGuide {
        return FullGuide(
            guide = Guide(0, 0, 0, 0, 2),
            poi = POI(
                0,
                "une statue qui a fait la gloire de Muhlouse",
                "Une statue maintenant dans un square perdu mais elle a beaucoup changé de place. C'est un travailleur qui est usé par son travail",
                picture = uriAssetBuilder("statue_1.jpg"),
            ),
            trigger = Trigger(
                0,
                "statue",
                "", "", "Dans le parc, troisième rue à gauche après la gare"
            )
        )
    }

    private fun buildMulhouseGuide4Tour() : FullGuide {
        return FullGuide(
            guide = Guide(0, 0, 0, 0, 2),
            poi = POI(
                0,
                "La plus haute tour d'europe",
                "Il suffit de lever les yeux vers le ciel et on la voit qui domine la ville",
                picture = uriAssetBuilder("tower_1.jpg"),
            ),
            trigger = Trigger(
                0,
                "tower",
                "", "", "Il faut regarder dans le ciel"
            )
        )
    }

    private fun buildMulhouseTour() : FullTour {
        return FullTour(
            tour = Tour (
                0,
                "Un tour mémoriel de Mulhouse",
                "Lors de ce tour vous allez pouvoir apercevoir toutes les particularités de cette magnifique ville de Mulhouse",
                uriAssetBuilder("mulhouse.png"),
                mode = TourMode.USER,
                withPicture = WithHelper.USER,
                withHint = WithHelper.USER,
                withDelay = WithHelper.USER,
                ),
            listOf(buildMulhouseGuide1Tour(), buildMulhouseGuide2Tour(), buildMulhouseGuide3Tour(), buildMulhouseGuide4Tour())
        )
    }

    fun provideTour () : FullTour? {
        return buildMulhouseTour()
    }

}