%% CALCULATE EVIDENCE FROM PROBABILITY AND CERTAINTY
% a: probability of the trust value
% c: certainty of the trust value
% y: return amount of evidence of the trust value
function y=inverse(a,c)
m=0; n=1920;
if c>=certainty(n*a,n*(1-a))
    y=n;
    return;
end
k=(m+n)/2;
while abs(certainty(k*a,k*(1-a))-c)>0.00001
    if c < certainty(k*a,k*(1-a))
        n=k;
    else
        m=k;
    end
    k=(m+n)/2;
end

y=k;




