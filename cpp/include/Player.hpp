#ifndef PLAYER_HPP
#define PLAYER_HPP

#include "Hand.hpp"
#include "Action.hpp"
#include "GameState.hpp"
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

    Action lastAction;

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
    Action getLastAction() const;

    void setActive(bool active);
    void setDealer(bool dealer);
    void setPosition(int pos);

    void fold();
    void check();
    void call(int amount);
    void bet(int amount);
    void raise(int amount);
    void allIn();

    void addToBalance(int amount);
    void subtractFromBalance(int amount);
    bool canAfford(int amount) const;

    void resetLastAction();

    void clearHand();
    void showHand() const; 

    const Hand& getHand() const;
    void receiveCard(const Card& card);

    virtual Decision makeDecision(const GameState& state) = 0;

    void recordHandPlayed(bool won, int profit);
    double getWinRate() const;
    int getTotalProfit() const;
    int getHandsPlayed() const;
    int getHandsWon() const;
    void resetStatistics();

    virtual ~Player() = default;
};

#endif