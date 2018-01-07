%% CERTAINTY OF TRUST
% r,s: trust value
% y: return certainty of the given trust value
function y=certainty(r,s)
if (r==0) && (s==0)
    y=0;
else if (r==0)
        pcdff = @(x)(1-x).^s;
        a = quad(pcdff,0 ,1);
        ff = @(z) abs((1-z).^s -a)/2;
        y=quad(ff,0,1)/a; 
    else if (s==0)
                pcdff = @(x) x.^r;
                a = quad(pcdff,0 ,1);
                ff = @(z) abs(z.^r -a)/2;
                y=quad(ff,0,1)/a; 
        else
pcdff = @(x) ((r+s).*x./r).^r.*((r+s).*(1-x)./s).^s;
a = quad(pcdff,0,(r/(r+s))^4)+quad(pcdff,(r/(r+s))^4,(r/(r+s))^2)+quad(pcdff,(r/(r+s))^2, (r/(r+s))) + quad(pcdff,(r/(r+s)),1-(s/(r+s))^2)+ quad(pcdff,1-(s/(r+s))^2,1-(s/(r+s))^4)+ quad(pcdff,1-(s/(r+s))^4,1);
ff = @(z) abs(((r+s).*z./r).^r.*((r+s).*(1-z)./s).^s -a)/2;
y=(quad(ff,0,(r/(r+s))^4)+quad(ff,(r/(r+s))^4,(r/(r+s))^2)+quad(ff,(r/(r+s))^2, (r/(r+s))) + quad(ff,(r/(r+s)),1-(s/(r+s))^2)+ quad(ff,1-(s/(r+s))^2,1-(s/(r+s))^4)+ quad(ff,1-(s/(r+s))^4,1))/a; 

        end
    end
end