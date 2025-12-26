#include "ComputerPlayer.hpp"
#include <cstdlib>
#include <ctime>
using namespace std;

ComputerPlayer::ComputerPlayer(
    const string& playerName,
    int initialBalance,
    HardnessLevel level
) : Player(playerName, initialBalance), level(level) {
    static bool seeded = false;
    if (!seeded) {
        srand(static_cast<unsigned>(time(nullptr)));
        seeded = true;
    }
}

Decision ComputerPlayer::makeDecision(const GameState& state) {
    int balance = getBalance();
    Decision decision{ Action::NONE, 0 };

    const Hand& hand = state.hand;
    int toCall = state.currentBet - getCurrentBet();
    if (toCall < 0) toCall = 0;
    int minRaise = state.minRaise;

    switch (level) {

    case HardnessLevel::EASY: {
        int r = rand() % 3;

        if (r == 0) {
            fold();
            decision.action = Action::FOLD;
        }
        else if (r == 1 && canAfford(toCall)) {
            call(toCall);
            decision.action = Action::CALL;
            decision.amount = toCall;
        }
        else {
            if (toCall == 0) {
                check();
                decision.action = Action::CHECK;
            } else {
                fold();
                decision.action = Action::FOLD;
            }
        }
        break;
    }

    case HardnessLevel::MEDIUM: {
        if (state.street == Street::PREFLOP) {
            if (hand.hasPocketPair() || hand.isHighCard()) {
                if (canAfford(minRaise)) {
                    raise(minRaise);
                    decision.action = Action::RAISE;
                    decision.amount = minRaise;
                } else if (canAfford(toCall)) {
                    call(toCall);
                    decision.action = Action::CALL;
                    decision.amount = toCall;
                } else {
                    fold();
                    decision.action = Action::FOLD;
                }
            } else {
                fold();
                decision.action = Action::FOLD;
            }
        }
        else {
            if (hand.isSuited() || hand.isConnector()) {
                if (canAfford(toCall)) {
                    call(toCall);
                    decision.action = Action::CALL;
                    decision.amount = toCall;
                } else {
                    fold();
                    decision.action = Action::FOLD;
                }
            } else {
                if (toCall == 0) {
                    check();
                    decision.action = Action::CHECK;
                } else {
                    fold();
                    decision.action = Action::FOLD;
                }
            }
        }
        break;
    }

    case HardnessLevel::HARD: {
        bool strongHand =
            hand.hasPocketPair() ||
            hand.isHighCard() ||
            (hand.isSuited() && hand.isConnector());

        if (strongHand) {
            if (balance <= minRaise) {
                decision.amount = balance;
                allIn();
                decision.action = Action::ALL_IN;
            }
            else {
                int raiseAmount = minRaise * 2;
                if (canAfford(raiseAmount)) {
                    raise(raiseAmount);
                    decision.action = Action::RAISE;
                    decision.amount = raiseAmount;
                } else if (canAfford(toCall)) {
                    call(toCall);
                    decision.action = Action::CALL;
                    decision.amount = toCall;
                } else {
                    fold();
                    decision.action = Action::FOLD;
                }
            }
        }
        else {
            if (toCall == 0) {
                check();
                decision.action = Action::CHECK;
            } else {
                fold();
                decision.action = Action::FOLD;
            }
        }
        break;
    }
    }

    return decision;
}
