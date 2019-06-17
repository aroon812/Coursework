% Test Network 1
%
% Solution should be as follows:
%
% Red edge sequences:
% [b,d,f,h,k]
% [b,d,f,h,i,k]
% [b,d,i,k]
% 
% Alternating edge sequences:
% [b,d,f,h,j]
% [a,f,h,j]
% [a,d,f,g,j]
% [a,d,e,i,k]
% [b,d,f,g,j]
% [b,d,e,i,k]
% 
% Matching edge/state sequences:
% [a,f,g,h,j]
% [b,d,i,k]

state(a, green).
state(b, red).
state(c, red).
state(d, red).
state(e, green).
state(f, green).
state(g, green).
state(h, green).
state(i, red).
state(j, green).
state(k, red).

start_state(a).
start_state(b).

end_state(j).
end_state(k).

trans(a, c, green).
trans(a, f, green).
trans(a, d, green).
trans(b, d, red).
trans(b, d, green).
trans(d, f, red).
trans(d, f, green).
trans(d, i, red).
trans(d, e, red).
trans(e, i, green).
trans(f, g, green).
trans(f, h, red).
trans(g, h, green).
trans(g, j, red).
trans(h, j, green).
trans(h, k, red).
trans(h, i, red).
trans(i, k, red).

