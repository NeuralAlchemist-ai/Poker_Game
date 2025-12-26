#include "Table.hpp"
#include <stdexcept>
using namespace std;

Table::Table() : pot(0), sidePot(0), currentBet(0) {}

void Table::addCard(const Card& card) {
    if (cards.size() < 5) {
        cards.push_back(card);
    } else {
        throw out_of_range("There are 5 cards on the table");
    }
}

Card Table::getFlopCard(int index) const {
    if (index < 0 || index >= 3 || cards.size() < 3) {
        throw out_of_range("Invalid flop card index");
    }
    return cards[index];
}

const vector<Card>& Table::getAllFlopCards() const {
    int s = cards.size();
    if (s != 3) {
        throw out_of_range("There are not 3 cards on the table");
    }
    return cards;
}

Card Table::getTurnCard() const {
    if (cards.size() < 4) {
        throw out_of_range("It is preturn");
    }
    return cards[3];
}

Card Table::getRiverCard() const {
    if (cards.size() < 5) {
        throw out_of_range("It is preriver");
    }
    return cards[4];
}

const vector<Card>& Table::getAllCards() const {
    return cards;
}

void Table::clear() {
    cards.clear();
}

size_t Table::cardCount() const {
    return cards.size();
}

void Table::addToPot(int amount) {
    if (amount < 0) {
        throw invalid_argument("Cannot add negative amount to pot");
    }
    pot += amount;
}

int Table::getPot() const {
    return pot;
}

void Table::resetPot() {
    pot = 0;
    currentBet = 0;
}

void Table::setCurrentBet(int bet) {
    if (bet < 0) {
        throw invalid_argument("Bet cannot be negative");
    }
    currentBet = bet;
}

int Table::getCurrentBet() const {
    return currentBet;
}

bool Table::hasFlop() const {
    return cards.size() >= 3;
}

bool Table::hasTurn() const {
    return cards.size() >= 4;
}

bool Table::hasRiver() const {
    return cards.size() >= 5;
}