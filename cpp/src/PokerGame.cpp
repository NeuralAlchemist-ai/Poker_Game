#include "PokerGame.hpp"
#include <algorithm>
#include <stdexcept>

using namespace std;

PokerGame::PokerGame()
    : pot(0),
      sidePot(0),
      currentBet(0),
      minRaise(10),
      street(Street::PREFLOP),
      dealerIndex(0) {}

void PokerGame::addPlayer(unique_ptr<Player> player) {
    players.push_back(move(player));
}

void PokerGame::startNewHand() {
    resetHand();
    deck.shuffle();
    dealHoleCards();
}

void PokerGame::playHand() {
    startNewHand();

    runBettingRound();
    if (isHandOver()) return;

    street = Street::FLOP;
    dealCommunityCards();
    runBettingRound();
    if (isHandOver()) return;

    street = Street::TURN;
    dealCommunityCards();
    runBettingRound();
    if (isHandOver()) return;

    street = Street::RIVER;
    dealCommunityCards();
    runBettingRound();

    resolveShowdown();
}

void PokerGame::resetHand() {
    pot = 0;
    sidePot = 0;
    currentBet = 0;
    street = Street::PREFLOP;
    communityCards.clear();
    deck.reset();

    for (auto& p : players) {
        p->clearHand();
        p->resetLastAction();
        p->setActive(true);
    }
}

void PokerGame::dealHoleCards() {
    for (int i = 0; i < 2; ++i) {
        for (auto& p : players) {
            p->receiveCard(deck.draw());
        }
    }
}

void PokerGame::dealCommunityCards() {
    if (street == Street::FLOP) {
        communityCards.push_back(deck.draw());
        communityCards.push_back(deck.draw());
        communityCards.push_back(deck.draw());
    } else {
        communityCards.push_back(deck.draw());
    }
}

void PokerGame::runBettingRound() {
    currentBet = 0;

    for (auto& p : players) {
        if (!p->isPlayerActive()) continue;

        GameState state = buildGameState(*p);
        Decision d = p->makeDecision(state);

        switch (d.action) {
        case Action::FOLD:
            p->fold();
            break;

        case Action::CHECK:
            break;

        case Action::CALL:
            pot += d.amount;
            currentBet = max(currentBet, d.amount);
            break;

        case Action::BET:
        case Action::RAISE:
            pot += d.amount;
            currentBet = d.amount;
            break;

        case Action::ALL_IN:
            pot += d.amount;
            break;

        default:
            break;
        }
    }
}

bool PokerGame::isHandOver() const {
    return countActivePlayers() == 1;
}

int PokerGame::countActivePlayers() const {
    int count = 0;
    for (const auto& p : players) {
        if (!p->hasPlayerFolded()) {
            ++count;
        }
    }
    return count;
}

GameState PokerGame::buildGameState(const Player& player) const {
    return GameState{
        player.getHand(),
        communityCards,
        currentBet,
        minRaise,
        pot,
        sidePot,
        street
    };
}

void PokerGame::resolveShowdown() {
    vector<Player*> contenders;

    for (auto& p : players) {
        if (!p->hasPlayerFolded()) {
            contenders.push_back(p.get());
        }
    }

    if (contenders.empty()) return;

    awardPot(*contenders.front());
}

void PokerGame::awardPot(Player& player) {
    player.addToBalance(pot + sidePot);
    pot = 0;
    sidePot = 0;
}