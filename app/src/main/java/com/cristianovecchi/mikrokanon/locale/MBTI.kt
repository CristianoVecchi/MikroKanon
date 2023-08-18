package com.cristianovecchi.mikrokanon.locale

// I (Moon) contains 6 <-> E (Sun) contains 0
// S (Venus) contains 4,8 <-> N (Saturn) contains 1,11
// T (Mars) contains 3,9 <-> F (Jupiter) contains 2,10
// P (Mercury) add 5,7
enum class MBTI(val character: String, val intervals: Set<Int>){
    ISTJ("The Inspector", setOf(6, 4,8, 3,9 )),
    ISTP("The Crafter", setOf(6, 4,8, 3,9 ,5,7 )),
    ISFJ("The Protector", setOf(6, 4,8, 2,10 )),
    ISFP("The Artist", setOf(6, 4,8, 2,10  ,5,7 )),
    INFJ("The Advocate", setOf(6, 1,11, 2,10  )),
    INFP("The Mediator", setOf(6, 1,11, 2,10  ,5,7 )),
    INTJ("The Architect", setOf(6, 1,11, 3,9  )),
    INTP("The Thinker", setOf(6, 1,11, 3,9  ,5,7 )),
    ESTP("The Persuader", setOf(0, 4,8, 3,9 ,5,7  )),
    ESTJ("The Director", setOf(0, 4,8, 3,9  )),
    ESFP("The Performer", setOf(0, 4,8, 2,10 ,5,7  )),
    ESFJ("The Caregiver", setOf(0, 4,8, 2,10  )),
    ENFP("The Champion", setOf(0, 1,11, 2,10  ,5,7 )),
    ENFJ("The Giver", setOf(0, 1,11, 2,10  )),
    ENTP("The Debater", setOf(0, 1,11, 3,9  ,5,7 )),
    ENTJ("The Commander", setOf(0, 1,11, 3,9  ));
    fun stringOfPlanets(planets: List<String>): String {
        //println("Planets: $planets")
        val sb = StringBuilder()
        if(intervals.contains(1)) sb.append(planets[0]).append(" ") // Saturn
        if(intervals.contains(2)) sb.append(planets[1]).append(" ") // Jupiter
        if(intervals.contains(3)) sb.append(planets[2]).append(" ") // Mars
        if(intervals.contains(4)) sb.append(planets[3]).append(" ") // Venus
        if(intervals.contains(5)) sb.append(planets[4]).append(" ") // Mercury
        if(intervals.contains(6)) sb.append(planets[5]).append(" ") // Moon
        if(intervals.contains(0)) sb.append(planets[6]).append(" ") // Sun
        return sb.toString()
    }
    companion object {
        fun listFromIntervals(intervals: Set<Int>): List<MBTI> {
            //println("intervals from MBTI: $intervals")
            val unique = values().filter{ intervals == it.intervals}
            if(unique.size == 1) return unique
            val list = values().filter{ intervals.containsAll(it.intervals) }
            //return list
            return if(intervals.containsAll(listOf(5,7))) list.filter{ it.intervals.containsAll(listOf(5,7))}
            else list.filter{ !it.intervals.containsAll(listOf(5,7))}
        }
        fun intervalsFromIndices(indices: List<Int>): Set<Int>{
            return values().filterIndexed{i, _-> indices.contains(i)}
                .fold(mutableListOf<Int>()){ acc, v -> acc.addAll(v.intervals); acc}.toSortedSet()
        }

    }

}