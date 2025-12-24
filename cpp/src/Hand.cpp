#include "Hand.hpp"
#include <algorithm>
using namespace std;

void Hand::addCard(const Card& card) {
    cards.push_back(card);
}

void Hand::clear() {
    cards.clear();
}

size_t Hand::size() const {
    return cards.size();
}

bool Hand::isEmpty() const {
    return cards.empty();
}

const Card& Hand::getCard(std::size_t index) const {
    return cards[index];
}

const vector<Card>& Hand::getAllCards() const {
    return cards;
}

void Hand::sortByRank() {
    sort(cards.begin(), cards.end(), Card::compareByRank);
}

void Hand::sortBySuit() {
    sort(cards.begin(), cards.end(), Card::compareBySuit);
}

void Hand::sortBestFirst() {
    sort(cards.begin(), cards.end(), greater<Card>());
}

bool Hand::hasPocketPair() const {
    if (cards.size() < 2) return false;

    return cards[0].getRank() == cards[1].getRank();
}

bool Hand::isSuited() const {
    if (cards.size() < 2) return false;

    return cards[0].getSuit() == cards[1].getSuit();
}

bool Hand::isConnector() const {
    if (cards.size() < 2) return false;

    int r0 = static_cast<int>(cards[0].getRank());
    int r1 = static_cast<int>(cards[1].getRank());

    if (r0 - r1 == 1 || r0 - r1 == -1) return true;

    return false;
}

bool Hand::isHighCard() const {
    return cards[0].getRank() >= Rank::JACK || cards[1].getRank() >= Rank::JACK;
}

bool Hand::containsRank(Rank rank) const {
    return cards[0].getRank() == rank || cards[1].getRank() == rank;
}

int Hand::countRank(Rank rank) const {
    if (cards[0].getRank() == rank && cards[1].getRank() == rank) return 2;
    else if (cards[0].getRank() != rank && cards[1].getRank() != rank) return 0;
    return 1;
}

bool Hand::containsSuit(Suit suit) const {
    return cards[0].getSuit() == suit || cards[1].getSuit() == suit;
}

int Hand::countSuit(Suit suit) const {
    if (cards[0].getSuit() == suit && cards[1].getSuit() == suit) return 2;
    else if (cards[0].getSuit() != suit && cards[1].getSuit() != suit) return 0;
    return 1;
}