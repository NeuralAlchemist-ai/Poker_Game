#ifndef COMPUTERPLAYER_HPP
#define COMPUTERPLAYER_HPP

#include "Player.hpp"
#include "HardnessLevel.hpp"
#include <string>

class ComputerPlayer : public Player 
{
private:
    HardnessLevel level;

public:
    ComputerPlayer(
        const std::string& playerName,
        int initialBalance,
        HardnessLevel level
    );
    Decision makeDecision(const GameState& state) override;
};

#endif