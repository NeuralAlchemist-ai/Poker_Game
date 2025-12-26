#include "HumanPlayer.hpp"
#include <iostream>
#include <limits>
#include <stdexcept>

using namespace std;

Decision HumanPlayer::makeDecision(const GameState& state) {
    int minRaise = state.minRaise;
    Decision decision{};
    char d = '\0';

    while (true) {
        cout << "Choose action:\n"
             << "f (fold)\n"
             << "c (check)\n"
             << "C (call)\n"
             << "b (bet)\n"
             << "a (all-in)\n> ";

        cin >> d;

        if (!cin) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            continue;
        }

        if (d == 'f' || d == 'c' || d == 'C' || d == 'b' || d == 'a') {
            break;
        }

        cout << "Invalid action. Try again\n";
    }

    switch (d) {
    case 'f':
        fold();
        decision.action = Action::FOLD;
        decision.amount = 0;
        break;

    case 'c':
        check();
        decision.action = Action::CHECK;
        decision.amount = 0;
        break;

    case 'C':
        call(minRaise);
        decision.action = Action::CALL;
        decision.amount = 0;
        break;

    case 'b': {
        int amount = 0;

        while (true) {
            cout << "Enter bet amount: ";
            cin >> amount;

            if (!cin || amount <= 0 || !canAfford(amount)) {
                cin.clear();
                cin.ignore(numeric_limits<streamsize>::max(), '\n');
                cout << "Invalid amount. Try again.\n";
                continue;
            }
            break;
        }

        bet(amount);
        decision.action = Action::BET;
        decision.amount = amount;
        break;
    }

    case 'a': {
        int amount = getBalance();
        allIn();
        decision.action = Action::ALL_IN;
        decision.amount = amount;
        break;
    }
    }

    return decision;
}