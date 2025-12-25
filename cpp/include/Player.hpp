#ifndef PLAYER_HPP
#define PLAYER_HPP

#include "Hand.hpp"
#include "Action.hpp"
#include <string>

class Player
{
private:
    std::string name;
    int balance;
    int currentBet;
    Hand hand;
    bool isActive;
    bool hasFolded;
    bool isAllIn;
    bool isDealer;
    int position;

    int handsPlayed;
    int handsWon;
    int totalProfit;

public:
    Player(const std::string& playerName, int initialBalance);
    Player(const Player&) = default;
    Player& operator=(const Player&) = default;

    const std::string& getName() const;
    int getBalance() const;
    int getCurrentBet() const;
    bool isPlayerActive() const;
    bool hasPlayerFolded() const;
    bool isPlayerAllIn() const;
    bool isPlayerDealer() const;
    int getPosition() const;

    void setActive(bool active);
    void setDealer(bool dealer);
    void setPosition(int pos);

    void addToBalance(int amount);
    void subtractFromBalance(int amount);
    bool canAfford(int amount) const;

    void clearHand();
    void showHand() const; 

    // virtual Decision makeDecision(int currentBet, int minRaise) = 0;

    void recordHandPlayed(bool won, int profit);
    double getWinRate() const;
    int getTotalProfit() const;
    int getHandsPlayed() const;
    int getHandsWon() const;
    void resetStatistics();

    virtual ~Player() = default;
};

#endif