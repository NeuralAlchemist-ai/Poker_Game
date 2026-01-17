#include "Card.hpp"
#include <iostream>
#include <sstream>
#include <vector>
#include <algorithm>
#include <map>
#include <string>

using namespace std;

int rankValue(char r) {
    switch (r) {
        case '2': return 2; case '3': return 3; case '4': return 4; case '5': return 5;
        case '6': return 6; case '7': return 7; case '8': return 8; case '9': return 9;
        case 'T': return 10; case 'J': return 11; case 'Q': return 12; case 'K': return 13; case 'A': return 14;
    }
    return 0;
}

struct SimpleCard { int rank; char suit; };

SimpleCard parseCard(const string& s) {
    char r = s[0];
    char su = s[1];
    return { rankValue(r), su };
}

long long evaluate7(const vector<SimpleCard>& cards) {
    vector<int> counts(15, 0); 
    map<char, vector<int>> bySuit;
    for (auto &c : cards) {
        counts[c.rank]++;
        bySuit[c.suit].push_back(c.rank);
    }

    int flushSuit = 0;
    vector<int> flushRanks;
    for (auto &p : bySuit) {
        if ((int)p.second.size() >= 5) {
            flushSuit = 1;
            flushRanks = p.second;
            sort(flushRanks.begin(), flushRanks.end());
            flushRanks.erase(unique(flushRanks.begin(), flushRanks.end()), flushRanks.end());
        }
    }

    auto findStraight = [&](vector<int> ranks)->int{
        if (ranks.empty()) return 0;
        sort(ranks.begin(), ranks.end());
        ranks.erase(unique(ranks.begin(), ranks.end()), ranks.end());
        if (find(ranks.begin(), ranks.end(), 14) != ranks.end()) ranks.insert(ranks.begin(), 1);
        int best = 0; int consec = 1; int last = ranks[0];
        for (size_t i=1;i<ranks.size();++i){
            if (ranks[i]==last+1) { consec++; last=ranks[i]; }
            else { consec=1; last=ranks[i]; }
            if (consec>best) best=consec;
        }
        if (best>=5) return last; 
        return 0;
    };

    int sfHigh = 0;
    if (flushSuit) {
        sfHigh = findStraight(flushRanks);
    }

    if (sfHigh) {
        return 900000000LL + sfHigh;
    }

    vector<pair<int,int>> occ; 
    for (int r=2;r<=14;++r) if (counts[r]>0) occ.push_back({counts[r], r});
    sort(occ.begin(), occ.end(), [](auto &a, auto &b){ if (a.first!=b.first) return a.first>b.first; return a.second>b.second; });

    if (!occ.empty() && occ[0].first==4) {
        int quad = occ[0].second;
        int kicker=0; for (int r=14;r>=2;--r) if (r!=quad && counts[r]>0) { kicker=r; break; }
        return 800000000LL + quad*100 + kicker;
    }

    if (occ.size()>=2 && (occ[0].first==3 && (occ[1].first>=2))) {
        int three = occ[0].second; int pair = occ[1].second;
        return 700000000LL + three*100 + pair;
    }

    if (flushSuit) {
        sort(flushRanks.begin(), flushRanks.end(), greater<int>());
        long long score = 600000000LL;
        int mult = 10000;
        for (int i=0;i<5 && i<(int)flushRanks.size();++i) { score += flushRanks[i]*mult; mult/=100; }
        return score;
    }

    vector<int> uniqRanks;
    for (int r=2;r<=14;++r) if (counts[r]>0) uniqRanks.push_back(r);
    int straightHigh = findStraight(uniqRanks);
    if (straightHigh) return 500000000LL + straightHigh;

    if (!occ.empty() && occ[0].first==3) {
        int three = occ[0].second;
        vector<int> kick;
        for (int r=14;r>=2;--r) if (r!=three && counts[r]>0) kick.push_back(r);
        long long score = 400000000LL + three*10000 + kick[0]*100 + (kick.size()>1?kick[1]:0);
        return score;
    }

    if (occ.size()>=2 && occ[0].first==2 && occ[1].first==2) {
        int highp = occ[0].second; int lowp = occ[1].second;
        int kicker=0; for (int r=14;r>=2;--r) if (r!=highp && r!=lowp && counts[r]>0) { kicker=r; break; }
        return 300000000LL + highp*10000 + lowp*100 + kicker;
    }

    if (!occ.empty() && occ[0].first==2) {
        int pair = occ[0].second;
        vector<int> kick;
        for (int r=14;r>=2;--r) if (r!=pair && counts[r]>0) kick.push_back(r);
        long long score = 200000000LL + pair*1000000 + kick[0]*10000 + kick[1]*100 + kick[2];
        return score;
    }

    vector<int> highs;
    for (int r=14;r>=2;--r) for (int k=0;k<counts[r] && highs.size()<5;++k) highs.push_back(r);
    long long score = 100000000LL;
    int mul = 1000000;
    for (int i=0;i<5 && i<(int)highs.size();++i){ score += highs[i]*mul; mul/=100; }
    return score;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string line;
    while (getline(cin, line)) {
        if (line.empty()) continue;
        if (line == "EXIT") break;
        if (line.rfind("EVAL ", 0) == 0) {
            string payload = line.substr(5);
            vector<string> parts;
            string cur;
            for (char ch : payload) { if (ch==';') { parts.push_back(cur); cur.clear(); } else cur.push_back(ch); }
            if (!cur.empty()) parts.push_back(cur);

            vector<SimpleCard> p1cards, p2cards, comm;
            if (parts.size() >= 1 && !parts[0].empty()) {
                string s = parts[0];
                stringstream ss(s);
                string token;
                while (getline(ss, token, ',')) if (!token.empty()) p1cards.push_back(parseCard(token));
            }
            if (parts.size() >= 2 && !parts[1].empty()) {
                string s = parts[1];
                stringstream ss(s);
                string token;
                while (getline(ss, token, ',')) if (!token.empty()) p2cards.push_back(parseCard(token));
            }
            if (parts.size() >= 3 && !parts[2].empty()) {
                string s = parts[2];
                stringstream ss(s);
                string token;
                while (getline(ss, token, ',')) if (!token.empty()) comm.push_back(parseCard(token));
            }

            vector<SimpleCard> all1 = p1cards; all1.insert(all1.end(), comm.begin(), comm.end());
            vector<SimpleCard> all2 = p2cards; all2.insert(all2.end(), comm.begin(), comm.end());

            long long score1 = evaluate7(all1);
            long long score2 = evaluate7(all2);

            if (score1 > score2) cout << "WINNER:1 SCORE:" << score1 << "\n";
            else if (score2 > score1) cout << "WINNER:2 SCORE:" << score2 << "\n";
            else cout << "WINNER:0 SCORE:" << score1 << "\n";
        } else {
            cout << "ERROR: Unknown command\n";
        }
        cout.flush();
    }

    return 0;
}
