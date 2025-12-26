#ifndef GAMESTATE_HPP
#define GAMESTATE_HPP

#include "Hand.hpp"
#include "Card.hpp"
#include "Street.hpp"
#include <vector>

struct GameState {
    const Hand& hand;                       
    const std::vector<Card>& community;     

    int currentBet;                         
    int minRaise;   

    int pot;      
    int sidePot;    
                          
    Street street;                          
};

#endif
