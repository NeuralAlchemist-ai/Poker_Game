#include "Player.hpp"
#include <string>
#include <iostream>
#include <stdexcept>
using namespace std;

Player::Player(const string& playerName, int initialBalance) 
    : name(playerName), balance(initialBalance),
      currentBet(0),
      hand(),
      isActive(true),
      hasFolded(false),
      isAllIn(false),
      isDealer(false),
      position(-1),

      handsPlayed(0),
      handsWon(0),
      totalProfit(0)
{
    if (playerName.empty()) {
        name = "Player1";
    }
}

const string& Player::getName() const {
    return name;
}

int Player::getBalance() const {
    return balance;
}

int Player::getCurrentBet() const {
    return currentBet;
}

bool Player::isPlayerActive() const {
    return isActive;
}

bool Player::hasPlayerFolded() const {
    return hasFolded;
}

bool Player::isPlayerAllIn() const {
    return isAllIn;
}

bool Player::isPlayerDealer() const {
    return isDealer;
}

int Player::getPosition() const {
    return position;
}

void Player::setActive(bool active) {
    isActive = active;
}


void Player::setDealer(bool dealer) {
    isDealer = dealer;
}

void Player::setPosition(int pos) {
    position = pos;
}

void Player::addToBalance(int amount) {
    if (amount < 0) {
        throw invalid_argument("Negative amount");
    }
    balance += amount;
}

void Player::subtractFromBalance(int amount) {
    if (amount < 0) {
        throw invalid_argument("Negative amount");
    } 
    if (amount > balance) {
        throw runtime_error("Not enough money");
    }
    balance -= amount;
}

bool Player::canAfford(int amount) const {
    return balance >= amount;
}

void Player::clearHand() {
    currentBet = 0;
    hand.clear();
    isActive = true;
    hasFolded = false;
    isAllIn = false;
}

void Player::showHand() const {
    cout << name << "'s hand: ";

    if (hand.isEmpty()) {
        cout << "(empty)\n";
        return;
    }

    bool first = true;
    for (const Card& card : hand.getAllCards()) {
        if (!first) cout << " ";
        cout << card.toString();
        first = false;
    }

    cout << '\n';
}

void Player::recordHandPlayed(bool won, int profit) {
    if (won) {
        handsWon++;
    }
    totalProfit += profit;
    handsPlayed += 1;
}

double Player::getWinRate() const {
    if (handsPlayed == 0) {
        return 0.0;
    }
    return static_cast<double>(handsWon) / handsPlayed;
}

int Player::getTotalProfit() const {
    return totalProfit;
}

int Player::getHandsPlayed() const {
    return handsPlayed;
}

int Player::getHandsWon() const {
    return handsWon;
}

void Player::resetStatistics() {
    handsPlayed = 0;
    handsWon = 0;
    totalProfit = 0;
}