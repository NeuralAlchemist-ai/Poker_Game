#ifndef ACTION_HPP
#define ACTION_HPP

enum class Action { 
    FOLD, CHECK, CALL, BET, RAISE, ALL_IN
};

struct Decision {
    Action action;
    int amount;  
};

#endif