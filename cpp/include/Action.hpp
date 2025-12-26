#ifndef ACTION_HPP
#define ACTION_HPP

enum class Action { 
    FOLD, CHECK, CALL, BET, RAISE, ALL_IN, NONE
};

struct Decision {
    Action action;
    int amount;  
};

#endif