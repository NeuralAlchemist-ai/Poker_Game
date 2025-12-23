#ifndef POKERCOMBINATION_HPP
#define POKERCOMBINATION_HPP

#include "Rank.hpp"
#include "Suit.hpp"

enum class PokerCombination {
    HIGH_CARD,          
    ONE_PAIR,           
    TWO_PAIR,           
    THREE_OF_A_KIND,    
    STRAIGHT,           
    FLUSH,             
    FULL_HOUSE,        
    FOUR_OF_A_KIND,    
    STRAIGHT_FLUSH,    
    ROYAL_FLUSH 
};

#endif