package com.eighteen.eighteenandroid.domain.model

import com.eighteen.eighteenandroid.common.safeLet

data class Mbti(
    val energy: MbtiType.Energy,
    val information: MbtiType.Information,
    val decision: MbtiType.Decision,
    val lifestyle: MbtiType.Lifestyle
) {
    val mbtiString
        get() = listOf(
            energy,
            information,
            decision,
            lifestyle
        ).joinToString("") { it.alp.toString() }

    sealed interface MbtiType {
        val alp: Char

        sealed interface Energy : MbtiType {
            object Extroversion : Energy {
                override val alp: Char = 'E'
            }

            object Introversion : Energy {
                override val alp: Char = 'I'
            }
        }

        sealed interface Information : MbtiType {
            object Sensing : Information {
                override val alp: Char = 'S'
            }

            object Intuition : Information {
                override val alp: Char = 'N'
            }
        }

        sealed interface Decision : MbtiType {
            object Thinking : Decision {
                override val alp: Char = 'T'
            }

            object Feeling : Decision {
                override val alp: Char = 'F'
            }
        }

        sealed interface Lifestyle : MbtiType {
            object Judging : Lifestyle {
                override val alp: Char = 'J'
            }

            object Perceiving : Lifestyle {
                override val alp: Char = 'P'
            }
        }
    }

    companion object {
        val mbtiTypes = listOf(
            MbtiType.Energy.Extroversion, MbtiType.Energy.Introversion,
            MbtiType.Information.Sensing, MbtiType.Information.Intuition,
            MbtiType.Decision.Thinking, MbtiType.Decision.Feeling,
            MbtiType.Lifestyle.Judging, MbtiType.Lifestyle.Perceiving
        )
    }
}

fun createMbtiOrNull(mbtiTypeList: List<Mbti.MbtiType>): Mbti? {
    mbtiTypeList.run {
        val energy = filterIsInstance<Mbti.MbtiType.Energy>().firstOrNull()
        val information = filterIsInstance<Mbti.MbtiType.Information>().firstOrNull()
        val decision = filterIsInstance<Mbti.MbtiType.Decision>().firstOrNull()
        val lifestyle = filterIsInstance<Mbti.MbtiType.Lifestyle>().firstOrNull()
        return safeLet(
            energy,
            information,
            decision,
            lifestyle
        ) { e, i, d, l ->
            Mbti(
                energy = e,
                information = i,
                decision = d,
                lifestyle = l
            )
        }
    }
}

fun createMbtiOrNull(mbtiString: String): Mbti? {
    if (mbtiString.length != 4) return null
    val mbtiTypeList =
        mbtiString.mapNotNull { Mbti.mbtiTypes.find { type -> type.alp.equals(it, true) } }
    if (mbtiTypeList.size != 4) return null
    if (mbtiTypeList[0] !is Mbti.MbtiType.Energy) return null
    if (mbtiTypeList[1] !is Mbti.MbtiType.Information) return null
    if (mbtiTypeList[2] !is Mbti.MbtiType.Decision) return null
    if (mbtiTypeList[3] !is Mbti.MbtiType.Lifestyle) return null
    return createMbtiOrNull(mbtiTypeList)
}

