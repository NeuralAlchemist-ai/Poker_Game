#ifndef POKERGAME_HPP
#define POKERGAME_HPP

#include "Player.hpp"
#include "Deck.hpp"
#include "Card.hpp"
#include "GameState.hpp"
#include "Street.hpp"

#include <vector>
#include <memory>

class PokerGame {
private:
    std::vector<std::unique_ptr<Player>> players;
    Deck deck;
    std::vector<Card> communityCards;

    int pot;
    int sidePot;
    int currentBet;
    int minRaise;

    Street street;
    int dealerIndex;

private:
    void resetHand();
    void dealHoleCards();
    void dealCommunityCards();

    void runBettingRound();
    bool isHandOver() const;
    int countActivePlayers() const;

    GameState buildGameState(const Player& player) const;

    void resolveShowdown();
    void awardPot(Player& player);
    void awardPot(const std::vector<Player*>& winners);

public:
    PokerGame();

    void addPlayer(std::unique_ptr<Player> player);
    void startNewHand();
    void playHand();
};

#endif