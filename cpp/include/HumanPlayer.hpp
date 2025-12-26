#ifndef HUMANPLAYER_HPP
#define HUMANPLAYER_HPP

#include "Player.hpp"

class HumanPlayer : public Player 
{
    Decision makeDecision(const GameState& state) override;
};

#endif